package com.start.helm.initializr.spring;

import com.start.helm.util.DownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SpringInitializrProxy {

    @GetMapping(value = "/spring")
    public Object getCapabilities() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.valueOf("application/vnd.initializr.v2.2+json")));
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("https://start.spring.io/", HttpMethod.GET, entity, Object.class);
    }

    @GetMapping(value = "/spring/starter.zip")
    public Object getStarter(@RequestParam Map<String, String> requestParams) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, List<String>> collected = requestParams
                .keySet()
                .stream()
                .collect(Collectors.toMap(k -> k, k -> List.of(requestParams.get(k))));

        URI uri = UriComponentsBuilder.fromHttpUrl("https://start.spring.io/starter.zip")
                .queryParams(new MultiValueMapAdapter<>(collected))
                .build()
                .toUri();

        ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(uri, byte[].class);

        //TODO: generate a helm chart and put into zip

        if (forEntity.getStatusCode().is2xxSuccessful() && forEntity.getBody() != null) {
            ByteArrayResource resource = new ByteArrayResource(forEntity.getBody());
            return ResponseEntity.ok().headers(DownloadUtil.headers("starter.zip"))
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        }

        return ResponseEntity.internalServerError().build();
    }

}
