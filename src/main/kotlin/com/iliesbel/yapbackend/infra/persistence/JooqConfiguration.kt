package com.iliesbel.yapbackend.infra.persistence

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class JooqConfig(@Autowired val dataSource: DataSource) {

    @Bean
    fun dslContext(): DSLContext {
        return DSL.using(
            dataSource,
            SQLDialect.POSTGRES
        )
    }
}
