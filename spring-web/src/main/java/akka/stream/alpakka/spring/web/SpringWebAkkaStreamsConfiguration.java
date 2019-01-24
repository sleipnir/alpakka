/*
 * Copyright (C) 2016-2018 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.stream.alpakka.spring.web;

import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;

// #configure

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

@Configuration
@ConditionalOnClass(akka.stream.javadsl.Source.class)
@EnableConfigurationProperties(SpringWebAkkaStreamsProperties.class)
public class SpringWebAkkaStreamsConfiguration {

  private static final String DEFAULT_ACTORY_SYSTEM_NAME = "SpringWebAkkaStreamsSystem";
  
  private final ActorSystem system;
  private final ActorMaterializer mat;
  private final SpringWebAkkaStreamsProperties properties;

  public SpringWebAkkaStreamsConfiguration(final SpringWebAkkaStreamsProperties properties) {
	  this.properties = properties;
    final ReactiveAdapterRegistry registry = ReactiveAdapterRegistry.getSharedInstance();
   
    system = ActorSystem.create(getActorSystemName(properties));
    mat = ActorMaterializer.create(system);
    new AkkaStreamsRegistrar(mat).registerAdapters(registry);
  }

  @Bean
  @ConditionalOnMissingBean(ActorSystem.class)
  public ActorSystem getActorSystem() {
    return system;
  }

  @Bean
  @ConditionalOnMissingBean(Materializer.class)
  public ActorMaterializer getMaterializer() {
    return mat;
  }

  public SpringWebAkkaStreamsProperties getProperties() {
	  return properties;
  }
  
  private String getActorSystemName(final SpringWebAkkaStreamsProperties properties) {
    Objects.requireNonNull(properties, 
        String.format("%s is not present in application context", SpringWebAkkaStreamsProperties.class.getSimpleName()));
    
    if(isBlank(properties.getActorSystemName())) {
      return DEFAULT_ACTORY_SYSTEM_NAME;
    }
    
    return properties.getActorSystemName();
  }
 
  private boolean isBlank(String str) {
    return (str == null || str.isEmpty());
  }

}

// #configure
