import { DEV_ENDPOINT } from "./origin_dev";

export const register = async (
  username,
  password,
  repeatPassword,
  avatar,
  callback,
  errorCallback
) => {
  fetch(DEV_ENDPOINT + "/players/player/register", {
    method: "POST",
    body: JSON.stringify({
      username: username,
      password: password,
      repeatPassword: repeatPassword,
      avatar: avatar,
    }),
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
  })
    .then((response) => [
      response.headers.get("Authorization"),
      response.status !== 201 ? response.json() : null,
    ])
    .then((result) => {
      if (result[0]) {
        callback(result[0]);
      } else {
        errorCallback(result[1]);
      }
    })
    .catch((err) => {
      console.log("here", err);
      errorCallback(err);
    });
};
