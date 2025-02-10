import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
    vus: 500,        // 동시 요청 수
    duration: '30s', // 테스트 진행 시간
};

export default function () {
    const url = 'http://localhost:8989/api/v1/concerts/9/schedules';
    const headers = {'Queue-Token': '29f96251-56aa-4ce2-b15d-b0073422abe0'};
    const response = http.get(url, {headers: headers});
    // console.log(response.status);
    check(response, {
        'is status 200': (r) => r.status === 200,
    });
    sleep(1);
}