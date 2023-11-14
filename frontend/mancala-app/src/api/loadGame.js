import { DEV_ENDPOINT } from "./origin_dev";

export const loadGame = async (game, callback, errorCallback) => {
  fetch(DEV_ENDPOINT + "/game/active/" + localStorage.getItem("gameToken"), {
    method: "GET",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  })
    .then((response) => {
      if (response.status === 200) {
        return response.json();
      } else if (response.status === 404) {
        loadStoredGame(game, callback, errorCallback);
      }
    })
    .then((gameResponse) => {
      callback(gameResponse.game);
    })
    .catch((err) => {
      errorCallback(err.message);
    });
};

const loadStoredGame = async (game, callback, errorCallback) => {
  fetch(DEV_ENDPOINT + "/game/" + localStorage.getItem("gameToken"), {
    method: "GET",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  })
    .then((response) => {
      if (response.status === 200) {
        return response.json();
      }
    })
    .then((gameResponse) => {
      callback(gameResponse.game);
    })
    .catch((err) => {
      errorCallback(err.message);
    });
};
