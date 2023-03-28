# ⚠️ This repository is no longer maintained.

<img src="https://developer.nexmo.com/assets/images/Vonage_Nexmo.svg" height="48px" alt="Nexmo is now known as Vonage" />

## Support Notice
This is an archived repository. If you have any questions, feel free to reach out to us at devrel@vonage.com or through our Community Slack at https://developer.vonage.com/community/slack.

<hr />

## Santa Tracker SMS

An application for receiving SMS updates on Santa's wherabouts during Christmas Eve.

### Data

The data comes from the [Unnoficial API](https://santa-api.appspot.com/info?client=web) that Google uses for their annual Santa Tracker application.

Check out their repository for their web application: https://github.com/google/santa-tracker-web.

The data is retrieved using the `SantaLocationLookupService` when the application starts and is seeded into a local database. This URL will most likely stop working around Christmas Eve.

### Configuration

Copy the `application.example.properties` to `application.properties` and fill out the following information:

- `nexmo.api.key=your_api_key` Your Nexmo API Key
- `nexmo.api.secret=your_api_secret` Your Nexmo API Secret
- `geonames.user=your-geonames-user` Your GeoNames username.

### Starting

Run the `gradle bootRun` command to start the application.
