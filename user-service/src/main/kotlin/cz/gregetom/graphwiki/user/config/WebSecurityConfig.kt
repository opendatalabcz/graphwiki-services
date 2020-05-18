package cz.gregetom.graphwiki.user.config

import cz.gregetom.graphwiki.commons.security.AuthenticationEntryPoint
import cz.gregetom.graphwiki.commons.security.JwtRequestFilter
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.web.CommonRequestMappingConstants
import cz.gregetom.graphwiki.user.web.RequestMappingConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class WebSecurityConfig(
        private val authenticationEntryPoint: AuthenticationEntryPoint,
        private val jwtRequestFilter: JwtRequestFilter
) : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST,
                        RequestMappingConstants.UC_CREATE,
                        RequestMappingConstants.AC_AUTHENTICATE
                ).permitAll()
                .antMatchers(HttpMethod.GET,
                        CommonRequestMappingConstants.ENTRY_ACTIONS,
                        RequestMappingConstants.AC_AUTH_INFO,
                        RequestMappingConstants.UC_VALIDATION
                ).permitAll()
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.UC_FIND_BY_ROLE
                ).hasAnyAuthority(Roles.ROLE_TECHNICAL)
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.UC_FIND_BY_ID
                ).permitAll()
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.UC_LOGGED
                ).authenticated()
                .anyRequest().denyAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
