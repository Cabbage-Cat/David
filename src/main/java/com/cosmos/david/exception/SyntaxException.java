package com.cosmos.david.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SyntaxException extends RuntimeException {
    private String syntax;
    private List<Object> errorObject;
}
