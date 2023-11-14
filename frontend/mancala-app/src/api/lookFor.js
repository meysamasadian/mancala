import { DEV_ENDPOINT } from "./origin_dev";

export const lookFor = async (player, callback) => {
  fetch(DEV_ENDPOINT + "/game/find/player", {
    method: "GET",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  })
    .then((response) => {
      console.log(response, response.body);
      if (response.status === 200) {
        return response.json();
      }
    })
    .then((game) => {
      console.log("game", game);
      if (game) {
        callback(game);
      }
    });
};
