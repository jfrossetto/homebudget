package br.com.jfr.homebudget.api.v1;

import br.com.jfr.libs.commons.exception.Error;
import java.util.UUID;

public class ApiUtils {

  public static NotFoundException buildNotFoundError(String target, UUID id) {
    return new NotFoundException(
        new Error()
            .code("NotFound")
            .message("Not found " + target + " with id " + id)
            .target("id"));
  }


}
