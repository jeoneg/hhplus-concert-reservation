import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
    vus: 500,        // 동시 요청 수
    duration: '30s', // 테스트 진행 시간
};

export default function () {
    const url = 'http://localhost:8989/api/v1/reservations?status=CANCELLED&startDate=2025-02-11&endDate=2025-02-13';
    const headers = {'Queue-Token': 'c9bac468-2ce9-4b84-87b6-befe10f05c69'};
    const response = http.get(url, {headers: headers});
    check(response, {
        'is status 200': (r) => r.status === 200,
    });
    sleep(1);
}
