import React from "react";
import styles from "./basicInfo.module.css";

const BasicInfo = () => {
  return (
    <>
      <div className={styles.app}>
        <h2>Input your basic info:</h2>
        <form action="">
          <input
            className={styles.input}
            type="text"
            placeholder="Default input"
            aria-label="default input example"
          />
        </form>
      </div>
    </>
  );
};

export default BasicInfo;
