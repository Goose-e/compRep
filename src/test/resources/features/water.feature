Feature: Daily water norm calculation

  Scenario: Get temperature and calculate water norm
    Given water APIs are available
    When I request temperature for city "Minsk"
    And I calculate water norm with weight 70 and time 60 using received temperature
    Then response status should be 200
    And calculated water volume should be greater than 0

  Scenario Outline: Check weight and temperature combinations
    When I calculate water norm with weight <weight> and time 60 at temperature <temp>
    Then response status should be <status>
    And calculated water volume should be <comparison> <volume>

    Examples:
      | weight | temp | status | comparison | volume |
      | 70     | 20   | 200    | greater    | 0      |
      | 70     | 35   | 200    | greater    | 0      |
      | 2      | 20   | 400    | absent     | 0      |

  Scenario: Water norm increases when temperature is above 30°C
    When I calculate water norm with weight 70 and time 60 at temperature 30
    And I remember calculated water volume as "base"
    And I calculate water norm with weight 70 and time 60 at temperature 35
    Then response status should be 200
    And calculated water volume should be greater than remembered "base"
