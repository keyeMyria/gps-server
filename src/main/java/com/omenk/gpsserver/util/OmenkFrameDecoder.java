/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.omenk.gpsserver.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 *
 * @author omenkzz
 */
public class OmenkFrameDecoder extends FrameDecoder {

    private static Integer find(
            ChannelBuffer buf,
            Integer start,
            Integer length,
            String subString) {

        int index = start;
        boolean match;

        for (; index < length; index++) {
            match = true;

            for (int i = 0; i < subString.length(); i++) {
                char c = (char) buf.getByte(index + i);
                if (c != subString.charAt(i)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                return index;
            }
        }

        return null;
    }

    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        // Check minimum length
        int length = buf.readableBytes();
        if (length < 100) {
            return null;
        }

        // Find start
        Integer beginIndex = find(buf, 0, length, "GPRMC");
        if (beginIndex == null) {
            return null;
        }

        // Find identifier
        Integer idIndex = find(buf, beginIndex, length, "imei:");
        if (idIndex == null) {
            return null;
        }

        // Find end
        Integer endIndex = find(buf, idIndex, length, ",");
        if (endIndex == null) {
            return null;
        }

        // Read buffer
        buf.skipBytes(beginIndex);
        ChannelBuffer frame = buf.readBytes(endIndex - beginIndex + 1);

        return frame;
    }
}
