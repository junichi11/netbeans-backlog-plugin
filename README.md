# NetBeans Backlog plugin

This plugin provides support for [backlog](http://backlogtool.com/?lang=1).

## Download

- https://github.com/junichi11/netbeans-backlog-plugin/releases
- [NetBeans Pluign Portal](http://plugins.netbeans.org/plugin/56831/backlog)

## Requirements

- NetBeans 8.1+

## Features

- Add an issue
- Update an issue
- Create queries
- Find issues
- Schedules
- Notification (only comment)

## Add Backlog repository

1. Open the task window : Windows > Task
2. Click add repository icon
3. Select the Backlog Connector
4. Select a backlog domain (backlog.jp/backlogtool.com)
5. Input your space id and API key
6. Click Connect button
7. Select your project
8. Input display name
9. Click OK button

## Default queries

- Assigned to me
- Created by me

Issues of statuses other than Closed are shown.
If you don't want to use thses, you can disable these in Options (Tools > Options > Team > Backlog).

## NOTE

- Can't set a due date on context menu (Right-click an issue > Schedule for). Please set it on an issue panel.

## Resources

- [Backlog4j](https://github.com/nulab/backlog4j)

## Donation

<a href="https://www.patreon.com/junichi11"><img src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" height="50"></a>

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
