package com.kob.backend.service.user.bot;

import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import org.springframework.web.servlet.function.ServerRequestExtensionsKt;

import java.util.Map;

public interface RemoveService {
    Map<String, String> remove(Map<String, String> data);
}
