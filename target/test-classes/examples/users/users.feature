Feature: Todo API End-to-End Flow with Kafka Integration (ATDD)

  Background:
    * def config = karate.callSingle('classpath:karate-config.js')
    * def baseUrl = config.baseUrl
    * def KafkaUtils = Java.type('examples.users.KafkaUtils')
    * def kafkaUtils = new KafkaUtils(config.kafka.bootstrapServers, config.security.kafkaUsername, config.security.kafkaPassword)
    * def kafkaTopic = config.kafka.topics.todoEvents
    * def consumerGroup = config.kafka.consumerGroup
    * def timeoutMs = config.kafka.timeoutMs

    # Clear existing todos before each run
    * def all = karate.call('classpath:examples/users/todo-kafka.feature@getAllTodos')
    * eval
    """
    for (var i = 0; i < all.length; i++) {
      karate.call('classpath:examples/users/todo-kafka.feature@deleteTodo', { id: all[i].id });
    }
    """
    * eval kafkaUtils.consumeMessages(kafkaTopic, consumerGroup, 100, 2)

  @atdd
  Scenario: Create todo and verify Kafka event
    Given url baseUrl
    And request { title: 'First', complete: false }
    When method post
    Then status 200
    And match response == { id: '#string', title: 'First', complete: false }
    * def id = response.id

    # Kafka message verification
    * def kafkaMessage = kafkaUtils.waitForMessage(kafkaTopic, id, timeoutMs / 1000)
    * match kafkaMessage != null
    * match kafkaMessage.value contains '"eventType":"TODO_CREATED"'
    * match kafkaMessage.value contains '"todoId":"#(id)"'

  @atdd
  Scenario: CRUD flow and multiple Kafka messages
    # Create first todo
    * def todo1 = karate.call('classpath:examples/users/todo-kafka.feature@createTodo', { title: 'First', complete: false })
    * def id1 = todo1.id

    # Fetch by ID
    Given url baseUrl + '/' + id1
    When method get
    Then status 200
    And match response == { id: '#(id1)', title: 'First', complete: false }

    # Create second todo
    * def todo2 = karate.call('classpath:examples/users/todo-kafka.feature@createTodo', { title: 'Second', complete: false })
    * def id2 = todo2.id

    # Get all todos
    Given url baseUrl
    When method get
    Then status 200
    And match response contains [{ id: '#(id1)', title: 'First', complete: false }, { id: '#(id2)', title: 'Second', complete: false }]

    # Verify Kafka delivery for both messages
    * def msg1 = kafkaUtils.waitForMessage(kafkaTopic, id1, timeoutMs / 1000)
    * def msg2 = kafkaUtils.waitForMessage(kafkaTopic, id2, timeoutMs / 1000)
    * match msg1.value contains '"todoId":"#(id1)"'
    * match msg2.value contains '"todoId":"#(id2)"'

  @atdd @errorHandling
  Scenario: Kafka broker unavailable should fail gracefully
    * def original = config.kafka.bootstrapServers
    * def KafkaUtils = Java.type('examples.users.KafkaUtils')
    * def failUtils = new KafkaUtils('invalid-broker:9092', '', '')

    Given url baseUrl
    And request { title: 'Kafka Fail Test', complete: false }
    When method post
    Then status 200
    * def failed = karate.read('target/karate.log').contains('Failed to produce message')
    * assert failed == true