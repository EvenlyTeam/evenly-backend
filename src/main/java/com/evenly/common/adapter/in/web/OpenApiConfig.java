package com.evenly.common.adapter.in.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(title = "Evenly API", version = "0.0.1", description = "더치페이 모임 정산기 API"),
        servers = {@Server(url = "http://localhost:8080", description = "Local")})
class OpenApiConfig {}
