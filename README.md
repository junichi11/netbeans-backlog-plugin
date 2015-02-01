# NetBeans Backlog plugin

This plugin provides support for [backlog](http://backlogtool.com/?lang=1).

## Download

- https://github.com/junichi11/netbeans-backlog-plugin/releases

## Requirements

- NetBeans 8.0+

## Features

- Add an issue
- Update an issue
- Create queries
- Find issues
- Schedules

## Add Backlog repository

1. Open the task window : Windows > Task
2. Click add repository icon
3. Select the Backlog Connector
4. Input your space id and API key
5. Click Connect button
6. Select your project
7. Input display name
8. Click OK button

## Default queries

- Assigned to me
- Created by me

Issues of statuses other than Closed are shown.
If you don't want to use thses, you can disable these in Options (Tools > Options > Team > Backlog).

## NOTE

- Can't set a due date on context menu (Right-click an issue > Schedule for). Please set it on an issue panel.

## Resources

- [Backlog4j](https://github.com/nulab/backlog4j)
- [yenta](https://bitbucket.org/jglick/yenta)

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
