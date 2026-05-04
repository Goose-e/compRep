import pytest
import requests
from pathlib import Path
from pytest_bdd import scenarios, given, when, then, parsers

BASE_URL = "http://localhost:8080/api"
FEATURE_FILE = Path(__file__).parent / "water.feature"

scenarios(str(FEATURE_FILE))


class Context:
    def __init__(self):
        self.last_status = None
        self.last_body = None
        self.received_temp = None
        self.remembered = {}


@pytest.fixture
def ctx():
    return Context()


@given("water APIs are available")
def water_apis_are_available(ctx):
    assert ctx is not None


def _extract_volume(body):
    response_entity = body.get("responseEntity")
    if response_entity is None:
        return None
    return response_entity.get("volume")


@when(parsers.parse('I request temperature for city "{city}"'))
def request_temperature(ctx, city):
    response = requests.get(f"{BASE_URL}/weather/{city}")
    ctx.last_status = response.status_code
    ctx.last_body = response.json()
    ctx.received_temp = float(ctx.last_body["responseEntity"]["temp"])


@when(parsers.parse("I calculate water norm with weight {weight:d} and time {time:d} using received temperature"))
def calculate_with_received_temp(ctx, weight, time):
    response = requests.post(
        f"{BASE_URL}/water/norm",
        json={
            "weight": weight,
            "time": time,
            "temp": ctx.received_temp,
        },
    )
    ctx.last_status = response.status_code
    ctx.last_body = response.json()


@when(parsers.parse("I calculate water norm with weight {weight:d} and time {time:d} at temperature {temp:d}"))
def calculate_at_temp(ctx, weight, time, temp):
    response = requests.post(
        f"{BASE_URL}/water/norm",
        json={
            "weight": weight,
            "time": time,
            "temp": temp,
        },
    )
    ctx.last_status = response.status_code
    ctx.last_body = response.json()


@when(parsers.parse('I remember calculated water volume as "{key}"'))
def remember_volume(ctx, key):
    volume = _extract_volume(ctx.last_body)
    assert volume is not None, "Volume is missing in response"
    ctx.remembered[key] = float(volume)


@then(parsers.parse("response status should be {status:d}"))
def check_status(ctx, status):
    assert ctx.last_status == status


@then(parsers.parse("calculated water volume should be greater than {value:d}"))
def check_volume_greater_than(ctx, value):
    volume = _extract_volume(ctx.last_body)
    assert volume is not None, "Volume is missing in response"
    assert float(volume) > value


@then(parsers.parse("calculated water volume should be {comparison:w} {value:d}"))
def check_volume_comparison(ctx, comparison, value):
    volume = _extract_volume(ctx.last_body)

    if comparison == "absent":
        assert volume is None
    elif comparison == "greater":
        assert volume is not None, "Volume is missing in response"
        assert float(volume) > value
    else:
        raise AssertionError(f"Unsupported comparison: {comparison}")


@then(parsers.parse('calculated water volume should be greater than remembered "{key}"'))
def check_volume_greater_than_remembered(ctx, key):
    remembered = ctx.remembered.get(key)
    assert remembered is not None, f'Remembered value "{key}" was not saved'

    volume = _extract_volume(ctx.last_body)
    assert volume is not None, "Volume is missing in response"
    assert float(volume) > remembered