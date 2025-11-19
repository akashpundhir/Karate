Feature: Reusable Todo Helper Scenarios

  @createTodo
  Scenario: Create todo
    Given url config.baseUrl
    And request __arg
    When method post
    Then status 200
    And match response == { id: '#string', title: '#(title)', complete: '#(complete)' }
    * match result.status == 'success'
    * return response
    
    
    

  @getAllTodos
  Scenario: Get all todos
    Given url config.baseUrl
    When method get
    Then status 200
    * return response

  @deleteTodo
  Scenario: Delete todo by ID
    Given url config.baseUrl + '/' + __arg.id
    When method delete
    Then status 204
    
    * def msg = kafkaUtils.waitForMessage(topic, todoId, timeoutMs / 1000)
    * match result.status == 'success'