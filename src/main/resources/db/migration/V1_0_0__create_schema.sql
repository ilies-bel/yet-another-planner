CREATE TABLE context (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name VARCHAR(255) NOT NULL
);

-- Create project table
CREATE TABLE project (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name VARCHAR(255) NOT NULL
);

CREATE TABLE task (
                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      description TEXT,
                      status VARCHAR(50) NOT NULL,
                      difficulty INTEGER,
                      due_date TIMESTAMP,
                      context_id BIGINT NOT NULL,
                      project_id BIGINT NOT NULL,

                      CONSTRAINT fk_task_context
                          FOREIGN KEY (context_id)
                              REFERENCES context(id),

                      CONSTRAINT fk_task_project
                          FOREIGN KEY (project_id)
                              REFERENCES project(id)
);

