// 将链接信息存储在全局信息中
export default {
    state: {
        status: "matching", // 匹配界面 matching 对战界面 playing
        socket: null,
        // 对手信息
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
    },
    getters: {},
    // mutations 通常用来修改数据  采用commit调用
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGameMap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    actions: {},
    modules: {}
}