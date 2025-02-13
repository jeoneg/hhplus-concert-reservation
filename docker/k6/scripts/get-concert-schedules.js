import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
    vus: 500,        // 동시 요청 수
    duration: '30s', // 테스트 진행 시간
};

export default function () {
    const url = 'http://localhost:8989/api/v1/concerts/5555/schedules';
    const headers = {'Queue-Token': 'e301a2d6-19ae-454c-8981-28d67e490421'};
    const response = http.get(url, {headers: headers});
    check(response, {
        'is status 200': (r) => r.status === 200,
    });
    sleep(1);
}