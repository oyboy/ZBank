import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'https://sb2frontend-altenar2-stage.biahosted.com/api/SportsBook';

export const options = {
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.01'],
    },
    scenarios: {
        ramping_test: {
            executor: 'ramping-vus',
            startVUs: 1,
            stages: [
                { target: 2, duration: '20s' },
                { target: 5, duration: '20s' },
                { target: 10, duration: '20s' },
                { target: 20, duration: '20s' },
            ],
        },
    },
};

function buildQuery(params) {
    return Object.entries(params)
        .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
        .join('&');
}
function getAllSports(){
    const queryParams = {
        timezoneOffset: -180,
        langId: 8,
        skinName: 'betsonic',
        configId: 1,
        culture: 'en-gb',
        countryCode: 'NL',
        deviceType: 'Mobile',
        numformat: 'en',
        integration: 'skintest',
        period: 'periodall',
        hasLiveStream: false,
    };
    const res = http.get(`${BASE_URL}/GetAllSports?${buildQuery(queryParams)}`);
    check(res, { 'All sports status is 200': (r) => r.status === 200 });

    const rawItems = res.json('Result');
    return rawItems.map(item => item.Id);
}
function getLiveEvents(sportId) {
    const queryParams = {
        timezoneOffset: -180,
        langId: 8,
        skinName: 'betsonic',
        configId: 1,
        culture: 'en-gb',
        countryCode: 'NL',
        deviceType: 'Mobile',
        numformat: 'en',
        integration: 'skintest',
        sportids: sportId,
        categoryids: 0,
        champids: 0,
        group: 'Championship',
        outrightsDisplay: 'none',
        couponType: 0,
        marketGroupId: 0,
        filterSingleNodes: 2,
        hasLiveStream: false,
    };

    const res = http.get(`${BASE_URL}/GetLiveEvents?${buildQuery(queryParams)}`);
    check(res, { 'Live events status is 200': (r) => r.status === 200 });

    const rawItems = res.json('Result.Items');
    if (!Array.isArray(rawItems)) return [];

    return rawItems;
}

function getEventDetails(event) {
    const detailParams = {
        timezoneOffset: -180,
        langId: 8,
        skinName: 'betsonic',
        configId: 1,
        culture: 'en-gb',
        countryCode: 'NL',
        deviceType: 'Mobile',
        numformat: 'en',
        integration: 'skintest',
        eventId: event.Id,
        sportId: event.SportId,
    };

    const res = http.get(`${BASE_URL}/GetEventDetails?${buildQuery(detailParams)}`);
    check(res, { 'EventDetails status is 200': (r) => r.status === 200 });

    try {
        const group = res.json('Result.MarketGroups')?.find(g => g.Name === 'Main');
        if (group?.Items?.length) {
            for (const market of group.Items) {
                console.log(`Market: ${market.Name}`);
            }
        }
    } catch {}
}

export default function () {
    const sportIds = getAllSports();
    if (!sportIds.length) return;
    const sportId = sportIds[Math.floor(Math.random() * sportIds.length)];

    const events = getLiveEvents(sportId);
    if (!events.length) return;

    const randomEvent = events[Math.floor(Math.random() * events.length)];
    if (!randomEvent) return;

    console.log(`Sport ID: ${sportId} | Event: ${randomEvent.Id} | ${randomEvent.Name}`);
    getEventDetails(randomEvent);
    sleep(1);
}
