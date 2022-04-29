package com.beyontec.mdcp.exception;

import com.google.common.io.CharStreams;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.Reader;

public class BTSErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        System.out.println(response.toString());
        if (response.status() == HttpStatus.GONE.value()) {
            return new InvalidURLExpception(StringUtils.defaultIfEmpty(getExceptionMessage(response), "Invalid URL"));
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }

    private String getExceptionMessage(Response response) {
        String message = null;
        try (Reader reader = response.body().asReader()) {
            message = CharStreams.toString(reader);

            /*ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            ExceptionMessage exceptionMessage = mapper.readValue(result, ExceptionMessage.class);
            return exceptionMessage.getMessage();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

   /* @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExceptionMessage {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
    }*/

}
