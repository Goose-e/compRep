Feature: Daily water norm calculation

  Scenario: Get temperature and calculate water norm
    Given weather service is available
    When I request temperature for city "Minsk"
    And I calculate water norm with weight 70 and time 60
    Then response status should be 200
    And calculated water volume should be greater than 0

  Scenario Outline: Check different weight and temperature values
    When I calculate water norm with weight <weight> and time 60 at temperature <temp>
    Then response status should be <status>

    Examples:
      | weight | temp | status |
      | 70     | 20   | 200    |
      | 70     | 35   | 200    |
      | 2      | 20   | 400    |