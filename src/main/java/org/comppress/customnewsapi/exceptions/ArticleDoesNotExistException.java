package org.comppress.customnewsapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleDoesNotExistException extends RuntimeException{
    private String message;
    private String variable;
}
