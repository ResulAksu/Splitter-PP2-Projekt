package propra.splitter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringConfiguration {

  @Bean
  public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
    chainBuilder
        .authorizeHttpRequests(configure -> {
          try {
            configure.antMatchers("/api/**").permitAll().and().csrf().disable();
          } catch (Exception e) {
            System.err.println("SecurityFilterChain configuration error");
          }
        });
    chainBuilder
        .authorizeHttpRequests(configure -> configure.anyRequest().authenticated())
        .oauth2Login(Customizer.withDefaults());
    return chainBuilder.build();
  }
}