package io.github.stanxlab.codegen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbInfo {

    private String url;

    private String username;

    private String password;
}
