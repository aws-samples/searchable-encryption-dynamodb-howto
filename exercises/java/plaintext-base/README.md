

The entry point is `App.java`
There is a subCommand defined `PutEmployee.class,`

In the `PutEmployee.java` you can see this implemented command.

You can run `./employee-portal` and it will pass all parameters over.
It will also compile the jar on first use.
To compile it later, `gradle fatJar`

To add autocomplete to the CLI
`source <(./employee-portal generate-completion)`
