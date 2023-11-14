import { DEV_ENDPOINT } from "./origin_dev";

export const move = async (spotIndex, callback, errorCallback) => {
  console.log("Running move...", spotIndex);
  fetch(
    DEV_ENDPOINT +
      "/game/" +
      localStorage.getItem("gameToken") +
      "/move/" +
      spotIndex,
    {
      method: "PUT",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }
  )
    .then((response) => {
      console.log(response, response.body);
      if (response.status === 200) {
        return response.json();
      }
    })
    .then((result) => {
      console.log("result-game..............", result);
      return result
        ? callback(result.game)
        : errorCallback("You can't move this spot");
    })
    .catch((err) => {
      errorCallback(err.body);
    });
};
