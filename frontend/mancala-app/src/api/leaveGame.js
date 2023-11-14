import { DEV_ENDPOINT } from "./origin_dev";

export const leaveGame = async (player, callback, errorCallback) => {
  fetch(
    DEV_ENDPOINT + "/game/" + localStorage.getItem("gameToken") + "/leave",
    {
      method: "DELETE",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }
  )
    .then((response) => {
      console.log(response, response.body);
      return response.status === 200;
    })
    .then((result) => {
      console.log("result", result);
      return result ? callback(result) : errorCallback("Try later!");
    })
    .catch((err) => {
      errorCallback(err.body);
    });
};
