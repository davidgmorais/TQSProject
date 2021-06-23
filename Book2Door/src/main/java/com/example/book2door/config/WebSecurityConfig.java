package com.example.book2door.config;

import com.example.book2door.component.AuthEntryPoint;
import com.example.book2door.filter.AuthTokenFilter;
import com.example.book2door.service.AdminServiceImpl;
import com.example.book2door.service.ClientServiceImpl;
import com.example.book2door.service.StoreServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ClientServiceImpl userDetailsService;

    @Autowired
    StoreServiceImpl storeDetailsService;

    @Autowired
    AdminServiceImpl adminDetailsService;


    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return new AuthEntryPoint();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean(name = "ClientDetailsService")
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(adminDetailsService).passwordEncoder(bCryptPasswordEncoder());
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .antMatchers("/admin/**").hasRole("ADMIN")
        .antMatchers("/store/**").hasRole("STORE")
        .antMatchers("/**").permitAll()
        .anyRequest().authenticated()
        .and()
    .authorizeRequests();
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
