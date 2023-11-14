import { DEV_ENDPOINT } from "./origin_dev";

export const login = async (username, password, callback, errorCallback) => {
  fetch(DEV_ENDPOINT + "/players/player/authenticate", {
    method: "POST",
    body: JSON.stringify({
      username: username,
      password: password,
    }),
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
  })
    .then((response) =>
      response.status === 202 ? response.headers.get("Authorization") : null
    )
    .then((token) =>
      token
        ? callback(token)
        : errorCallback("Username or Password is not correct!")
    )
    .catch((err) => {
      errorCallback(err);
    });
};
