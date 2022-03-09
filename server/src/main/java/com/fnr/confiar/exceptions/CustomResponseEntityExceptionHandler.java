package com.fnr.confiar.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistsEntityException.class)
    public final ResponseEntity<ErrorDetails> handleEntityAlreadyExists(AlreadyExistsEntityException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<ErrorDetails> handleBusinessException(EntityNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(new ErrorDetails(LocalDateTime.now(), error, ex.getLocalizedMessage()), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        return buildResponseEntity(new ErrorDetails(LocalDateTime.now(), builder.substring(0, builder.length() - 2), ex.getLocalizedMessage()), UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Constraint violation",
                ex.getConstraintName() + " - " + ex.getLocalizedMessage());
                // ex.getConstraintViolations()
                //         .stream()
                //         .map(constraintViolation ->
                //                 constraintViolation.getPropertyPath() + " - " +
                //                         constraintViolation.getMessage())
                //         .collect(Collectors.joining()));
        return buildResponseEntity(errorDetails, BAD_REQUEST);
    }

    // TODO: try to get it work
    // @ExceptionHandler({ ResponseException.class, ConstraintViolationException.class, DataIntegrityViolationException.class })
    // public ResponseEntity<Response> handleBadRequest(WebRequest request, Exception exception) {
    //   ErrorResponse responseBody = null;
    //   Reflections reflections = new Reflections("com.fnr.confiar.models");
    //   Set<Class<? extends BaseModel>> classes = reflections.getSubTypesOf(BaseModel.class);
  
    //   Iterator<Class<? extends BaseModel>> classIterator = classes.iterator();
    //   while (classIterator.hasNext() && responseBody == null) {
    //     Class<? extends BaseModel> clazz = classIterator.next();
    //     String entityName = clazz.getSimpleName().replaceAll("Model", "").toUpperCase();
        
    //     if (exception.getLocalizedMessage().indexOf(entityName) > -1) {
    //       for (Field f : clazz.getDeclaredFields()) {
    //         if (exception.getLocalizedMessage().indexOf("(" + StringUtil.camelCaseToUnderscores(f.getName()).toUpperCase() + ")") > -1) {
    //           responseBody = new ErrorResponse(HttpStatus.CONFLICT, new BaseModel<BaseEntity>(null), messageService.getMessage("field.error." + f.getName()), f.getName());
    //           break;
    //         }
    //       }
    //     }
    //   }
  
    //   if (responseBody == null) {
    //     responseBody = new ErrorResponse(HttpStatus.CONFLICT, new BaseModel<BaseEntity>(null), messageService.getMessage("general.error"), null);
    //   }
  
    //   return ResponseEntity.status(responseBody.getStatus()).body(responseBody);
    // }
  
    private ResponseEntity<Object> buildResponseEntity(ErrorDetails error, HttpStatus status) {
        return new ResponseEntity<>(error, status);
    }

}
