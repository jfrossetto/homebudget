package br.com.jfr.homebudget.api.v1;

import br.com.jfr.libs.commons.api.exception.WebException;
import br.com.jfr.libs.commons.exception.Error;
import org.springframework.http.HttpStatus;

public class NotFoundException extends WebException {

    public NotFoundException(Error error) {
      super(HttpStatus.NOT_FOUND, error);
    }
}
