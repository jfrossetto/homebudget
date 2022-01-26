package br.com.jfr.homebudget.domain.uploadextract;

import br.com.jfr.libs.commons.exception.BusinessException;
import br.com.jfr.libs.commons.exception.Error;
import io.netty.util.internal.ObjectUtil;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportedExtractHeaderService {

  private final ImportedExtractHeaderRepository importedExtractHeaderRepository;

  public Mono<ImportedExtractHeader> uploadExtract(ImportedExtractHeader extractHeaderToImport,
                                                   Flux<ByteBuffer> extractFile) {
    return Mono.deferContextual(
        ctx -> {
          Mono<ImportedExtractHeader> saveHeader =  importedExtractHeaderRepository
              .save(extractHeaderToImport);

          Mono<List<String>> convertPdf = extractFile
              .flatMap(pdfByte -> Mono
                  .fromCallable(() -> convertPdfToText(pdfByte))
                  .subscribeOn(Schedulers.boundedElastic()))
              .single()
              //.doOnNext(pdfString -> log.info("pdf: \n {}", pdfString))
              .map(pdfString -> processPdfString(pdfString))
              .doOnNext(list -> list.forEach(line -> log.info("l: {}", line)))
              ;

          return Mono.zip(saveHeader, convertPdf)
              .map(tuple -> tuple.getT1());

        });
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

  private List<String> processPdfString(String pdfString) {
    final String[] toProcess = pdfString.split("\n");
    final List<String> clearExtractLine = new ArrayList<>();
    String[] extractParts = new String[3];
    for(int line=0; line < toProcess.length; line++) {
      if (isExtractValue(toProcess[line])) {
        if (!ObjectUtils.isEmpty(extractParts[0]) && !ObjectUtils.isEmpty(extractParts[1])) {
          clearExtractLine.add(extractParts[1] + " | " + extractParts[2] + " | " + extractParts[0]);
          if (extractParts[2].trim().equals("S A L D O")) {
            break;
          }
          extractParts = new String[3];
        }
        extractParts[0] = toProcess[line];
      } else if (ObjectUtils.isEmpty(extractParts[1])
                  && toProcess[line].length() >= 10 && isExtractDate(toProcess[line].substring(0, 10))) {
        extractParts[1] = toProcess[line].substring(0, 10);
      } else if (!ObjectUtils.isEmpty(extractParts[0])
                  && !ObjectUtils.isEmpty(extractParts[1])
                  && ObjectUtils.isEmpty(extractParts[2])) {
        extractParts[2] = toProcess[line-1].length() > 10
                          ? toProcess[line-1].substring(10).trim() + " " + toProcess[line].trim()
                          : toProcess[line].trim();
      }
    }
    return clearExtractLine;
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
