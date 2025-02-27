CREATE TABLE push_subscriptions (
                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      endpoint VARCHAR(255) NOT NULL,
                      p256dh  VARCHAR(255) NOT NULL,
                      auth  VARCHAR(255) NOT NULL
);

