package me.helmify.domain.ui.buildinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BuildInfo {

	private String version;

	private String id;

	private Date date;

}
