import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'https://sb2frontend-altenar2-stage.biahosted.com/api/SportsBook/GetUpcoming';

export const options = {
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(99)<1000'],
    },
    scenarios: {
        average_load: {
            executor: 'constant-arrival-rate',
            rate: 10,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 3,
        },
    }
};

export default function () {
    const defaultParams = {
        timezoneOffset: -180,
        langId: 39,
        skinName: 'betsonic',
        configId: 1,
        culture: 'fr-fr',
        countryCode: 'NL',
        deviceType: 'Mobile',
        numformat: 'en',
        integration: 'skintest',
        sportId: 74,
        showAllEvents: false,
        count: 10,
        hasStreaming: false,
    };

    const query = Object.entries(defaultParams)
        .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
        .join('&');

    const url = `${BASE_URL}?${query}`;
    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });
}