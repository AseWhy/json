package io.github.asewhy.json.support;

import io.github.asewhy.json.support.interfaces.iWriter;

public class CommonBuilderWriter implements iWriter {
    private final StringBuilder builder;

    public CommonBuilderWriter(StringBuilder stream) {
        this.builder = stream;
    }

    @Override
    public CommonBuilderWriter write(String chars) {
        builder.append(chars); return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
