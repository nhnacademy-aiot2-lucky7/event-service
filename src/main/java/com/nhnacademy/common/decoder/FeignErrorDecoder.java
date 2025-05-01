package com.nhnacademy.common.decoder;

import com.nhnacademy.common.exception.CommonHttpException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return new CommonHttpException(response.status(), "Feign 에러 발생");
    }
}

