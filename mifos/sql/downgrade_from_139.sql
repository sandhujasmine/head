DROP TABLE IF EXISTS ACCEPTED_PAYMENT_TYPE;

CREATE TABLE PRD_PMNT_TYPE (
  PRD_PMNT_TYPE_ID SMALLINT AUTO_INCREMENT NOT NULL,
  PAYMENT_TYPE_ID SMALLINT NOT NULL,
  ACCOUNT_TYPE_ID SMALLINT NOT NULL,
  ACCOUNT_ACTION_ID SMALLINT NOT NULL,
  IS_SUPPORTED SMALLINT,
  VERSION_NO SMALLINT NOT NULL,
  PRIMARY KEY(PRD_PMNT_TYPE_ID),
  FOREIGN KEY(PAYMENT_TYPE_ID)
    REFERENCES PAYMENT_TYPE(PAYMENT_TYPE_ID)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
  FOREIGN KEY(ACCOUNT_TYPE_ID)
    REFERENCES ACCOUNT_TYPE(ACCOUNT_TYPE_ID)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
  FOREIGN KEY(ACCOUNT_ACTION_ID)
    REFERENCES ACCOUNT_ACTION(ACCOUNT_ACTION_ID)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
)
ENGINE=InnoDB CHARACTER SET utf8;

update DATABASE_VERSION set DATABASE_VERSION = 138 where DATABASE_VERSION = 139;