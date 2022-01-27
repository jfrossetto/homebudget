package br.com.jfr.homebudget.domain.uploadextract;

import br.com.jfr.libs.commons.exception.BusinessException;
import br.com.jfr.libs.commons.exception.Error;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportedExtractHeaderService {

  private final ImportedExtractHeaderRepository importedExtractHeaderRepository;
  private final ImportedExtractLineRepository importedExtractLineRepository;

  @Transactional
  public Mono<ImportedExtractHeader> uploadExtract(ImportedExtractHeader extractHeaderToImport,
                                                   Flux<ByteBuffer> extractFile) {
    return Mono.deferContextual(
        ctx -> {
          Mono<ImportedExtractHeader> saveHeader =  importedExtractHeaderRepository
              .save(extractHeaderToImport)
              .doOnNext(saved -> log.info(" save extract header {}", saved.getId()));

          Mono<List<ImportedExtractLine>> convertPdf = extractFile
              .flatMap(pdfByte -> Mono.fromCallable(() -> processPdf(pdfByte)))
              .single();

          return Mono.zip(saveHeader,
                          convertPdf.subscribeOn(Schedulers.boundedElastic()))
              .flatMap(tuple -> {
                var savedHeader = tuple.getT1();
                var lines = tuple.getT2();
                return Flux.fromIterable(lines)
                    .flatMap(line -> {
                      line.setImportedExtractHeaderId(savedHeader.getId());
                      return importedExtractLineRepository
                          .save(line);
                          //.onErrorResume(ex -> Mono.error(
                          //    new BusinessException(new Error("PersistError","Error on persist imported_extractLine"))));
                    })
                    .count()
                    .doOnNext(count -> log.info(" {} lines saved", count))
                    .thenReturn(savedHeader);
              });

        });
  }

  private List<ImportedExtractLine> processPdf(ByteBuffer pdfByteBuffer) {
    return processPdfString(convertPdfToText(pdfByteBuffer));
  }

  private String convertPdfToText(ByteBuffer pdfByteBuffer) {
    try {
      log.info("Convert pdf to text");
      PDDocument pdf = PDDocument.load(new ByteArrayInputStream(pdfByteBuffer.array()));

      //PDFTextStripper pdfStripper = new PDFText2HTML();
      PDFTextStripper pdfStripper = new PDFTextStripper();
      pdfStripper.setStartPage(1);
      pdfStripper.setEndPage(pdf.getNumberOfPages());

      return pdfStripper.getText(pdf);

    } catch (Exception e) {
      log.error("Error in convert pdf to text ", e);
      throw new BusinessException(new Error("Error in convert pdf to text"));
    }
  }

  private List<ImportedExtractLine> processPdfString(String pdfString) {
    log.info("processPdfString");
    final String[] toProcess = pdfString.split("\n");
    final List<ImportedExtractLine> extractLine = new ArrayList<>();
    Predicate<String[]> addLine = (parts) -> !ObjectUtils.isEmpty(parts[0]) && !ObjectUtils.isEmpty(parts[1]);
    Predicate<String[]> finalize = (parts) -> parts[2].trim().equals("S A L D O");
    BiPredicate<String[], String> addDate = (parts, processLine) ->
        ObjectUtils.isEmpty(parts[1])
        && processLine.length() >= 10
        && isExtractDate(processLine.substring(0, 10));
    Predicate<String[]> addDescription = (parts) ->
        !ObjectUtils.isEmpty(parts[0])
        && !ObjectUtils.isEmpty(parts[1])
        && ObjectUtils.isEmpty(parts[2]);
    String[] extractParts = new String[3];
    Integer sequencial = 0;
    for(int line=0; line < toProcess.length; line++) {
      if (isExtractValue(toProcess[line])) {
        if (addLine.test(extractParts)) {
          extractLine.add(buildImportedExtractLines(extractParts, ++sequencial));
          if (finalize.test(extractParts)) {
            break;
          }
          extractParts = new String[3];
        }
        extractParts[0] = toProcess[line];
      } else if (addDate.test(extractParts, toProcess[line])) {
        extractParts[1] = toProcess[line].substring(0, 10);
      } else if (addDescription.test(extractParts)) {
        extractParts[2] = toProcess[line-1].length() > 10
                          ? toProcess[line-1].substring(10).trim() + " " + toProcess[line].trim()
                          : toProcess[line].trim();
      }
    }
    return extractLine;
  }

  private ImportedExtractLine buildImportedExtractLines(String[] extractParts, Integer sequencial) {
    String strToHash = extractParts[1] + " | " + extractParts[2] + " | " + extractParts[0];
    log.info("l: {}",strToHash);
    String hash = DigestUtils.md5DigestAsHex(strToHash.getBytes());
    BigDecimal signal = extractParts[0].contains("(-)") ? BigDecimal.valueOf(-1l) : BigDecimal.valueOf(1);
    String clearValue = extractParts[0]
        .replace(",","")
        .replace(".","")
        .replace("(+)","")
        .replace("(-)","")
        .trim();
    BigDecimal value = new BigDecimal(clearValue).divide(BigDecimal.valueOf(100l)).multiply(signal);
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate date = LocalDate.parse(extractParts[1], df);
    return ImportedExtractLine.builder()
        .entryDetail(extractParts[2].trim())
        .entryValue(value)
        .entryDate(date)
        .entryHash(hash)
        .sequentialId(sequencial)
        .build();
    /*
    of(Integer.valueOf(extractParts[1].substring(6)),
       Integer.valueOf(extractParts[1].substring(3, 5)),
       Integer.valueOf(extractParts[1].substring(0, 3)))
     */

  }

  private boolean isExtractValue(String value) {
    // value format: "3.150,99 (+)"
    Pattern regex = Pattern.compile("(?<!\\S)(?=.)(0|([1-9](\\d*|\\d{0,2}(.\\d{3})*)))?(\\,\\d*[0-9])?(\\s\\(\\+\\)|\\s\\(\\-\\))?(?!\\S)");
    return regex.matcher(value).matches();
  }

  private boolean isExtractDate(String value) {
    Pattern regex = Pattern.compile("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[1-9])|((1)[0-2]))(\\/)\\d{4}$");
    return regex.matcher(value).matches();
  }

}
