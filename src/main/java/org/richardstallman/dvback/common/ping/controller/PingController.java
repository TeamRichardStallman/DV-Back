package org.richardstallman.dvback.common.ping.controller;

import java.net.URI;
import org.richardstallman.dvback.common.DvApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class PingController {

  @Value("${python.server.url}")
  private String pythonServerUrl;

  @GetMapping("/ping")
  public ResponseEntity<DvApiResponse<String>> ping() {

    final RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    URI uri = UriComponentsBuilder.fromUriString(pythonServerUrl).path("/").build().toUri();
    return ResponseEntity.ok(DvApiResponse.of(restTemplate.getForObject(uri, String.class)));
  }

  @GetMapping("/ping-pong")
  public ResponseEntity<DvApiResponse<String>> pingPong() {

    return ResponseEntity.ok(DvApiResponse.of("pong"));
  }
}
