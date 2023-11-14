import React, { useState } from "react";

export const WaitingForCompetitor = (props) => {
  const [username, setUsername] = useState(props.player.username);
  const [avatar, setAvatar] = useState(props.player.avatar + ".png");

  return (
    <div className="waiting-container">
      <div className="profile-container">
        <div className="avatar-container">
          <img className="avatar-big" src={process.env.PUBLIC_URL + avatar} />
        </div>
        <h2>{username}</h2>
      </div>
      <div className="profile-container">
        <svg
          className="competitor"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 240 240"
          width={240}
          height={240}
        >
          <image
            id="avatar1"
            href="avatar1.png"
            width="240px"
            style={{ opacity: 0 }}
          >
            <animate
              id="animation1"
              attributeName="opacity"
              to="1"
              from="0"
              begin="0s;animation6.end"
              dur=".1s"
            />
          </image>
          <image
            id="avatar2"
            href="avatar2.png"
            width="240px"
            style={{ opacity: 0 }}
          >
            <animate
              id="animation2"
              attributeName="opacity"
              to="1"
              from="0"
              begin="animation1.end"
              dur=".1s"
            />
          </image>
          <image
            id="avatar3"
            href="avatar3.png"
            width="240px"
            style={{ opacity: 0 }}
          >
            <animate
              id="animation3"
              attributeName="opacity"
              to="1"
              from="0"
              begin="animation2.end"
              dur=".1s"
            />
          </image>
          <image
            id="avatar4"
            href="avatar4.png"
            width="240px"
            style={{ opacity: 0 }}
          >
            <animate
              id="animation4"
              attributeName="opacity"
              to="1"
              from="0"
              begin="animation3.end"
              dur=".1s"
            />
          </image>
          <image
            id="avatar5"
            href="avatar5.png"
            width="240px"
            style={{ opacity: 0 }}
          >
            <animate
              id="animation5"
              attributeName="opacity"
              from="0"
              to="1"
              begin="animation4.end"
              dur=".1s"
            />
          </image>
          <image
            id="avatar6"
            href="avatar6.png"
            width="240px"
            style={{ opacity: 0 }}
          >
            <animate
              id="animation6"
              attributeName="opacity"
              to="1"
              from="0"
              begin="animation5.end"
              dur=".1s"
            />
          </image>
        </svg>
        <h2>looking for..</h2>
      </div>
    </div>
  );
};
