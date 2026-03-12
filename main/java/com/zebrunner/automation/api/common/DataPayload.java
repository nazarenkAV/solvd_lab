package com.zebrunner.automation.api.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DataPayload<T> extends BasePayload<T> {

    private T data;

}
