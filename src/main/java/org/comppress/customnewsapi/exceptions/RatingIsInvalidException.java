package org.comppress.customnewsapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingIsInvalidException extends RuntimeException {
    private String message;
    private String variable;
}
