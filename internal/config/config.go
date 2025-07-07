package config

import (
	"fmt"
	"log"
	"os"
	"reflect"

	"github.com/joho/godotenv"
)

type EnvVars struct {
	CoinbaseApiKey    	  string
	CoinbaseApiSecret 	  string
	CoinbaseMarketDataUri string
}

func LoadEnv() (*EnvVars, error) {
	err := godotenv.Load()
	if err != nil {
		log.Println("error loading .env file, or it does not exist.")
	}

	envVars := &EnvVars{
		CoinbaseApiKey: os.Getenv("CB_API_KEY"),
		CoinbaseApiSecret: os.Getenv("CB_API_SECRET"),
		CoinbaseMarketDataUri: os.Getenv("CB_MARKET_DATA_URI"),
	}

	// validate that the env vars have been loaded 
	// - get all the fields using reflect package
	fieldValues := reflect.ValueOf(*envVars)

	for i := 0; i < fieldValues.NumField(); i++ {
		fieldValue := fieldValues.Field(i)
		if fieldValue.String() == "" {
			return nil, fmt.Errorf("%s env var not set", fieldValue)
		}
	}
	
	return envVars, nil
}