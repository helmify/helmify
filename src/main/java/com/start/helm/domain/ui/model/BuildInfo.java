package com.start.helm.domain.ui.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BuildInfo {

    String version;
    String id;
    Date date;

}
