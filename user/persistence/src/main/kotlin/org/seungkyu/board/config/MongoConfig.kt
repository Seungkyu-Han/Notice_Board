package org.seungkyu.board.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@EnableReactiveMongoAuditing
@Configuration
class MongoConfig {
}