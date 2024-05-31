package me.helmify.domain.helm.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InitContainer {

	private String name;

	private String image;

	private String imagePullPolicy;

	private Map<String, Object> securityContext;

	private List<String> command;

	private Map<String, Object> resources;

}
