function fn() {
  var env = karate.env || 'dev';
  karate.log('>>> Environment:', env);

  var config = {
    baseUrl: '',
    kafka: {
      bootstrapServers: '',
      topics: {
        todoEvents: 'todo-events-topic'
      },
      consumerGroup: 'todo-test-group',
      timeoutMs: 10000,
      maxMessages: 10
    },
    security: {
      kafkaUsername: '',
      kafkaPassword: ''
    }
  };

  if (env === 'dev') {
    config.baseUrl = 'http://localhost:8080/api';
    config.kafka.bootstrapServers = 'localhost:9092';
  } else if (env === 'stage') {
    config.baseUrl = 'https://stage-api.example.com/api';
    config.kafka.bootstrapServers = 'stage-kafka.example.com:9092';
    config.security.kafkaUsername = 'stage-user';
    config.security.kafkaPassword = 'stage-pass';
  } else {
    karate.fail('Unknown environment: ' + env);
  }

  karate.log('>>> baseUrl:', config.baseUrl);
  return config;
}