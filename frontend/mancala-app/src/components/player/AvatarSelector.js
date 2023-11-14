import React, { useState } from "react";

export const AvatarSelector = (props) => {
  const [avatarIndex, setAvatarIndex] = useState(3);
  const [rightVisible, setRightVisible] = useState("arrow");
  const [leftVisible, setLeftVisible] = useState("arrow");

  const avatars = [
    "avatar1.png",
    "avatar2.png",
    "avatar3.png",
    "avatar4.png",
    "avatar5.png",
    "avatar6.png",
  ];

  const arrowRight = (e) => {
    e.preventDefault();
    if (avatarIndex < 5) {
      let index = avatarIndex + 1;
      setAvatarIndex(index);
      hideOrShowArrows(index);
      props.onSelect(avatars[index].replace(".png", ""));
    }
  };

  const arrowLeft = (e) => {
    e.preventDefault();
    if (avatarIndex > 0) {
      let index = avatarIndex - 1;
      setAvatarIndex(index);
      hideOrShowArrows(index);
      props.onSelect(avatars[index].replace(".png", ""));
    }
  };

  const hideOrShowArrows = (index) => {
    if (index == 0) {
      setLeftVisible("arrow-hidden");
    } else {
      setLeftVisible("arrow");
    }

    if (index == 5) {
      setRightVisible("arrow-hidden");
    } else {
      setRightVisible("arrow");
    }
  };

  return (
    <div className="avatar-selector">
      <img className={leftVisible} src="arrow-left.png" onClick={arrowLeft} />
      <img
        className="avatar"
        src={process.env.PUBLIC_URL + avatars[avatarIndex]}
      />
      <img
        className={rightVisible}
        src="arrow-right.png"
        onClick={arrowRight}
      />
    </div>
  );
};
