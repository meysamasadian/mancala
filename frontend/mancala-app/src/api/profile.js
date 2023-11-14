import { DEV_ENDPOINT } from "./origin_dev";

export const getCurrentPlayer = async (token, callback, errorCallback) => {
  console.log("From input", token);
  console.log("From cache", localStorage.getItem("token"));
  fetch(DEV_ENDPOINT + "/players/player/", {
    method: "GET",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
      Authorization: "Bearer " + token,
    },
  })
    .then((response) => {
      console.log(response, response.body);
      return response.status === 200 ? response.json() : null;
    })
    .then((player) => {
      console.log("Player", player);
      return player ? callback(player) : errorCallback("Unathorized error!");
    })
    .catch((err) => {
      errorCallback(err);
    });
};
