package org.mi.common.core.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-21 16:11
 **/
public class LongSerializerModule extends SimpleModule {

    public LongSerializerModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(Long.class, ToStringSerializer.instance);
    }
}
